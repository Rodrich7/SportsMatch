# Flujo de trabajo y validación

## Regla para integrar cambios

Un cambio se propone mediante Pull Request hacia `master`. Antes del merge se debe:

1. Ejecutar localmente `gradlew test` (o `./gradlew test` en Linux/macOS) y revisar que termine con `BUILD SUCCESSFUL`.
2. Ejecutar la colección `postman/SportsMatch.postman_collection.json` en Postman y comprobar que todas las aserciones pasen.
3. Revisar que GitHub Actions termine en verde para el Pull Request.
4. Integrar el Pull Request solo después de esas comprobaciones.

El workflow `.github/workflows/ci.yml` ya ejecuta `./gradlew build --no-daemon` en Pull Requests dirigidos a `master` y en pushes a `master`. Ese check aporta una validación automática. Para impedir técnicamente un merge con checks fallidos, se debe configurar en GitHub una regla de protección de `master` que exija Pull Request y el check de CI; esa configuración aún no está confirmada.

## Prueba local con Postman

1. Iniciar SportsMatch desde la raíz del proyecto con Java 21: `gradlew.bat bootRun` en Windows o `./gradlew bootRun` en Linux/macOS.
2. Importar `postman/SportsMatch.postman_collection.json` en Postman.
3. Abrir **Collection Runner** y ejecutar la colección en orden contra `http://localhost:8080`.
4. Revisar las aserciones de cada solicitud. La colección genera un ID nuevo al crear el encuentro, así que puede repetirse cada vez que se reinicia el backend.

Este primer recorrido cubre creación, consulta, apertura de inscripciones, inscripción de un participante y rechazo de una inscripción duplicada. Se pueden agregar más casos después de comprobar esta colección básica.

## Ramas, issues y Pull Requests

- Actualizar la base de trabajo con `git switch master` y `git pull origin master`.
- Crear una rama pequeña por cambio, por ejemplo `git switch -c feature/nombre-del-cambio`.
- Usar commits descriptivos con prefijos como `feat:`, `fix:`, `test:`, `ci:` y `docs:`.
- Abrir un Pull Request hacia `master`, asociarlo a un issue real y cerrarlo con una referencia como `Closes #<numero>` cuando corresponda.
- Si `master` avanzó mientras se trabaja, integrarlo en la rama de cambio, resolver cualquier conflicto de forma explícita y volver a ejecutar las verificaciones.
- Crear tags y releases solo cuando exista una versión estable integrada en `master`; no asignar tags de release a cambios sin merge.

## Evidencia conocida al 2026-10-23

- El remoto configurado es `https://github.com/Rodrich7/SportsMatch.git`.
- El historial local contiene merges de Pull Requests anteriores, incluidos los PR #5, #6, #7 y #8.
- El workflow de CI está presente en `master`.
- En `feature/postman-validacion-api`, `gradlew build --no-daemon` pasó y el flujo HTTP local respondió correctamente en 10/10 comprobaciones. La colección de Postman está preparada, pero aún falta ejecutarla en Postman.
- No se encontró un tag de release ni un changelog antes de añadir este archivo. La existencia de un GitHub Project y la protección efectiva de `master` deben confirmarse en la configuración del repositorio.
