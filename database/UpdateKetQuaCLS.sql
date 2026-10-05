ALTER TABLE dbo.KetQuaCLS ADD HinhAnhFile NVARCHAR(MAX) NULL;
GO
EXEC sp_refreshview 'dbo.vw_KetQuaCLS';
GO

