# Refactorización de Validación de PIN: Simplificación de Operaciones de Negocio (Issue #44)

## Resumen
Este documento describe la transición técnica de una lógica de validación personalizada y compleja (`@ValidAtmPin`) a un enfoque estándar de alto rendimiento utilizando las restricciones de Jakarta Bean Validation para las operaciones transaccionales dentro del microservicio `ms-accounts`.

## Análisis Comparativo: De lo Personalizado a lo Estándar

| Característica | Antes (Legado) | Ahora (Refactorizado) |
| :--- | :--- | :--- |
| **Fuente** | Anotación Personalizada `@ValidAtmPin` | Validación Estándar de Jakarta |
| **Implementación** | `AtmPinValidator.java` (Lógica Personalizada) | `@NotNull`, `@NotBlank`, `@Pattern` |
| **Lógica** | Reglas de validación opacas/pesadas | Regex Transparente: `\\d{4}` |
| **Experiencia del Dev** | Más difícil de depurar y extender | Estándar, declarativo y predecible |
| **Mensajes de Error** | Varios/Inconsistentes | Estandarizados en Inglés (Según norma) |

## Beneficios Técnicos

### 1. Optimización de Rendimiento mediante Regex `\\d{4}`
Al reemplazar las clases validadoras personalizadas con una Expresión Regular nativa (`\\d{4}`), logramos:
- **Validación Instantánea**: La validación se realiza en la frontera (DTO) sin activar la carga de clases adicionales o lógica compleja.
- **Cumplimiento Estricto**: Refuerza simultáneamente la longitud (exactamente 4) y el tipo de carácter (solo numérico) en una sola pasada.

### 2. Estandarización y Mantenibilidad
- **Cumplimiento de Idioma**: Todos los mensajes de validación y la documentación se han alineado al inglés, cumpliendo estrictamente con los estándares de `CONTRIBUTING.md`.
- **Desacoplamiento**: Las operaciones de negocio (`DEPOSIT`, `WITHDRAWAL`, `TRANSFER`) ahora están desacopladas de las reglas de PIN "Administrativas", lo que permite una evolución independiente de los requisitos de validación.

### 3. Pruebas Unitarias Puras
Se ha introducido una suite de pruebas dedicada bajo `src/test/java/.../dto/requests/` que utiliza `jakarta.validation.Validator` para probar las restricciones del DTO de forma aislada. Esto evita la sobrecarga de un contexto completo de Spring Security/Web, resultando en tiempos de ejecución en el rango de milisegundos.

## Guía de Ejecución de Pruebas

Para verificar la integridad de estos cambios y asegurar que no haya regresiones, ejecute el siguiente comando de Maven:

```bash
mvn test -Dtest="TransactionRequestTest,TransferRequestTest"
```

> [!NOTE]
> Estas pruebas cubren escenarios positivos (PINs válidos de 4 dígitos) y múltiples vectores negativos (nulo, vacío, espacios, caracteres alfabéticos y longitudes incorrectas).

---
**Autor:** Senior Backend Engineer  
**Alcance:** MS-Accounts / Issue #44
