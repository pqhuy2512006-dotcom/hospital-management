use QuanLyBenhVien
go

CREATE OR ALTER PROCEDURE dbo.usp_Entity_Save
    @EntityName SYSNAME,
    @Payload NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;

    IF ISJSON(@Payload) <> 1
        THROW 50001, 'Entity payload must be valid JSON.', 1;

    DECLARE @TableName SYSNAME = CASE @EntityName
        WHEN N'AuditLog' THEN N'AuditLog'
        WHEN N'BenhNhan' THEN N'BenhNhan'
        WHEN N'ChiTietHoaDon' THEN N'ChiTietHoaDon'
        WHEN N'ChiTietKetQuaCLS' THEN N'ChiTietKetQuaCLS'
        WHEN N'ChiTietNhapKho' THEN N'ChiTietNhapKho'
        WHEN N'DichVuCLS' THEN N'DichVuCLS'
        WHEN N'DonThuoc' THEN N'DonThuoc'
        WHEN N'GiuongBenh' THEN N'GiuongBenh'
        WHEN N'HoaDon' THEN N'HoaDon'
        WHEN N'KetQuaCLS' THEN N'KetQuaCLS'
        WHEN N'Khoa' THEN N'Khoa'
        WHEN N'LichHen' THEN N'LichHen'
        WHEN N'LichTruc' THEN N'LichTruc'
        WHEN N'NhanVien' THEN N'NhanVien'
        WHEN N'NoiTru' THEN N'NoiTru'
        WHEN N'PhieuKham' THEN N'PhieuKham'
        WHEN N'PhieuNhapKho' THEN N'PhieuNhapKho'
        WHEN N'Thuoc' THEN N'Thuoc'
        WHEN N'YeuCauChuyenKhoa' THEN N'YeuCauChuyenKhoa'
        ELSE NULL
    END;

    IF @TableName IS NULL
        THROW 50002, 'Entity is not allowed for generic persistence.', 1;

    DECLARE @ObjectId INT = OBJECT_ID(N'dbo.' + QUOTENAME(@TableName), N'U');
    DECLARE @KeyColumn SYSNAME;
    SELECT @KeyColumn = c.name
    FROM sys.key_constraints kc
    JOIN sys.index_columns ic ON ic.object_id = kc.parent_object_id AND ic.index_id = kc.unique_index_id
    JOIN sys.columns c ON c.object_id = ic.object_id AND c.column_id = ic.column_id
    WHERE kc.parent_object_id = @ObjectId AND kc.type = N'PK';

    IF @KeyColumn IS NULL
        THROW 50003, 'Entity table has no primary key.', 1;

    DECLARE @QualifiedTable NVARCHAR(300) = N'dbo.' + QUOTENAME(@TableName);
    DECLARE @KeyValue NVARCHAR(4000) = JSON_VALUE(@Payload, N'$.' + QUOTENAME(@KeyColumn, '"'));
    DECLARE @InsertColumns NVARCHAR(MAX);
    DECLARE @InsertValues NVARCHAR(MAX);
    DECLARE @UpdateSet NVARCHAR(MAX);

    SELECT
        @InsertColumns = STRING_AGG(CONVERT(NVARCHAR(MAX), QUOTENAME(c.name)), N',') WITHIN GROUP (ORDER BY c.column_id),
        @InsertValues = STRING_AGG(CONVERT(NVARCHAR(MAX), N'(SELECT [value] FROM OPENJSON(@Payload) WHERE [key] = N''' + REPLACE(c.name, N'''', N'''''') + N''')'), N',') WITHIN GROUP (ORDER BY c.column_id)
    FROM sys.columns c
    WHERE c.object_id = @ObjectId
      AND c.is_identity = 0
      AND c.is_computed = 0
      AND c.generated_always_type = 0
    AND EXISTS (SELECT 1 FROM OPENJSON(@Payload) j
            WHERE j.[key] COLLATE DATABASE_DEFAULT = c.name COLLATE DATABASE_DEFAULT);

    SELECT @UpdateSet = STRING_AGG(
        CONVERT(NVARCHAR(MAX), QUOTENAME(c.name) + N' = (SELECT [value] FROM OPENJSON(@Payload) WHERE [key] = N''' + REPLACE(c.name, N'''', N'''''') + N''')'),
        N',') WITHIN GROUP (ORDER BY c.column_id)
    FROM sys.columns c
    WHERE c.object_id = @ObjectId
      AND c.name <> @KeyColumn
      AND c.is_identity = 0
      AND c.is_computed = 0
      AND c.generated_always_type = 0
    AND EXISTS (SELECT 1 FROM OPENJSON(@Payload) j
            WHERE j.[key] COLLATE DATABASE_DEFAULT = c.name COLLATE DATABASE_DEFAULT);

    IF @KeyValue IS NOT NULL
    BEGIN
        DECLARE @Exists BIT = 0;
        DECLARE @ExistsSql NVARCHAR(MAX) = N'SELECT @Found = CASE WHEN EXISTS (SELECT 1 FROM ' + @QualifiedTable
            + N' WHERE ' + QUOTENAME(@KeyColumn) + N' = @Value) THEN 1 ELSE 0 END;';
        EXEC sys.sp_executesql @ExistsSql,
            N'@Value NVARCHAR(4000), @Found BIT OUTPUT', @Value = @KeyValue, @Found = @Exists OUTPUT;

        IF @Exists = 1
        BEGIN
            IF @UpdateSet IS NULL RETURN;
            DECLARE @UpdateSql NVARCHAR(MAX) = N'UPDATE ' + @QualifiedTable + N' SET ' + @UpdateSet
                + N' WHERE ' + QUOTENAME(@KeyColumn) + N' = @Value;';
            EXEC sys.sp_executesql @UpdateSql,
                N'@Payload NVARCHAR(MAX), @Value NVARCHAR(4000)', @Payload = @Payload, @Value = @KeyValue;
            RETURN;
        END
    END

    IF @InsertColumns IS NULL
        THROW 50004, 'No writable entity columns found in payload.', 1;

    DECLARE @InsertSql NVARCHAR(MAX) = N'INSERT INTO ' + @QualifiedTable + N' (' + @InsertColumns
        + N') SELECT ' + @InsertValues + N';';
    EXEC sys.sp_executesql @InsertSql, N'@Payload NVARCHAR(MAX)', @Payload = @Payload;
END;
GO

CREATE OR ALTER PROCEDURE dbo.usp_Entity_Delete
    @EntityName SYSNAME,
    @IdValue NVARCHAR(4000)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @TableName SYSNAME = CASE @EntityName
        WHEN N'AuditLog' THEN N'AuditLog'
        WHEN N'BenhNhan' THEN N'BenhNhan'
        WHEN N'ChiTietHoaDon' THEN N'ChiTietHoaDon'
        WHEN N'ChiTietKetQuaCLS' THEN N'ChiTietKetQuaCLS'
        WHEN N'ChiTietNhapKho' THEN N'ChiTietNhapKho'
        WHEN N'DichVuCLS' THEN N'DichVuCLS'
        WHEN N'DonThuoc' THEN N'DonThuoc'
        WHEN N'GiuongBenh' THEN N'GiuongBenh'
        WHEN N'HoaDon' THEN N'HoaDon'
        WHEN N'KetQuaCLS' THEN N'KetQuaCLS'
        WHEN N'Khoa' THEN N'Khoa'
        WHEN N'LichHen' THEN N'LichHen'
        WHEN N'LichTruc' THEN N'LichTruc'
        WHEN N'NhanVien' THEN N'NhanVien'
        WHEN N'NoiTru' THEN N'NoiTru'
        WHEN N'PhieuKham' THEN N'PhieuKham'
        WHEN N'PhieuNhapKho' THEN N'PhieuNhapKho'
        WHEN N'Thuoc' THEN N'Thuoc'
        WHEN N'YeuCauChuyenKhoa' THEN N'YeuCauChuyenKhoa'
        ELSE NULL
    END;

    IF @TableName IS NULL
        THROW 50002, 'Entity is not allowed for generic persistence.', 1;

    DECLARE @ObjectId INT = OBJECT_ID(N'dbo.' + QUOTENAME(@TableName), N'U');
    DECLARE @KeyColumn SYSNAME;
    SELECT @KeyColumn = c.name
    FROM sys.key_constraints kc
    JOIN sys.index_columns ic ON ic.object_id = kc.parent_object_id AND ic.index_id = kc.unique_index_id
    JOIN sys.columns c ON c.object_id = ic.object_id AND c.column_id = ic.column_id
    WHERE kc.parent_object_id = @ObjectId AND kc.type = N'PK';

    IF @KeyColumn IS NULL
        THROW 50003, 'Entity table has no primary key.', 1;

    DECLARE @DeleteSql NVARCHAR(MAX) = N'DELETE FROM dbo.' + QUOTENAME(@TableName)
        + N' WHERE ' + QUOTENAME(@KeyColumn) + N' = @Value;';
    EXEC sys.sp_executesql @DeleteSql, N'@Value NVARCHAR(4000)', @Value = @IdValue;
END;
GO
