# Programación de Plataformas Móviles - Laboratorio No. 8

Demostración de Funcionamiento: https://youtube.com/shorts/yhS23eywnsE?feature=share

El principal objetivo de esta actividad agregar persistencia local con Room a una galeria de fotos y modelar un esquema mínimo de base de datos local para resultados paginados, favoritos y queries recientes.

Decisiones de modelado

- **Estructura:** Room se organiza en `data/`, separando entidades, DAOs y base de datos (`AppDatabase.kt`, `RecentSearchDao.kt`, `CachedPhotoDao.kt`, etc.).  
- **Caché local:** DAOs como `CachedPhotoDao` y `FavoriteLocalDao` permiten acceso offline.  
- **Sin ViewModels:** el estado se maneja directamente en composables/pantallas (`views/`), usando `Flows` y controlando el ciclo de vida manualmente.  
