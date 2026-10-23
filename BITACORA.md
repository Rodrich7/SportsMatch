# Bitácora de avances

Registro breve de trabajo individual y verificaciones reproducibles.

## 2026-10-23

- Se revisó la rama `feature-cqrs-consulta-encuentro` y se confirmó que apunta a `origin/feature-cqrs-consulta-encuentro`.
- Se reubicaron el controlador y las solicitudes HTTP bajo `presentation/web` en el commit `bda4c91`.
- Se creó la rama `feature/postman-validacion-api` desde `origin/master` para mantener separado el trabajo del curso.
- Se ejecutó `gradlew build --no-daemon` con Java 21 en esa rama: `BUILD SUCCESSFUL`.
- Se inició la API de esa rama localmente y se comprobó por HTTP el flujo de creación, consulta, inscripción y cambios de estado. Pasaron 10/10 comprobaciones: creación `201`, ID duplicado `409`, consulta y transiciones válidas `200`, participante duplicado `400`.
- Se preparó una colección inicial de cinco solicitudes para probar creación, consulta, apertura e inscripción, incluida la validación de participante duplicado. Aún debe importarse y ejecutarse en la aplicación Postman para guardar evidencia de esa herramienta.

## Próxima verificación

- Ejecutar la colección desde Postman contra `http://localhost:8080` y registrar el resultado del Collection Runner antes de solicitar el merge.
- Revisar que el check de GitHub Actions esté verde en el Pull Request.
- Confirmar en GitHub si `master` exige Pull Request y el check de CI antes de permitir merges.
