### Endpoints de `ReservationController`

1. **Obtener todas las reservas**

   - **Método**: `GET`
   - **URL**: `/reservations/admin`
   - **Descripción**: Obtiene todas las reservas. Solo accesible para administradores.
   - **Autenticación**: Requiere autenticación.
   - **Roles Permitidos**: Administrador (`ROLE_ADMIN`).

2. **Obtener historial de reservas del usuario**

   - **Método**: `GET`
   - **URL**: `/reservations/my`
   - **Descripción**: Obtiene el historial de reservas del usuario autenticado.
   - **Autenticación**: Requiere autenticación.
   - **Roles Permitidos**: Todos los usuarios autenticados.

3. **Obtener una reserva por ID**

   - **Método**: `GET`
   - **URL**: `/reservations/{reservationId}`
   - **Descripción**: Obtiene los detalles de una reserva específica por su ID. Los usuarios solo pueden obtener sus propias reservas. Los administradores pueden obtener cualquier reserva.
   - **Autenticación**: Requiere autenticación.
   - **Roles Permitidos**: Todos los usuarios autenticados (limitado a sus propias reservas), Administrador (`ROLE_ADMIN`) puede acceder a cualquier reserva.

4. **Crear una nueva reserva**

   - **Método**: `POST`
   - **URL**: `/reservations`
   - **Descripción**: Crea una nueva reserva. Calcula el monto basado en las fechas de inicio y fin.
   - **Autenticación**: Requiere autenticación.
   - **Roles Permitidos**: Todos los usuarios autenticados.
   - **Datos del cuerpo**:
     ```json
     {
       "productId": 1,
       "startDate": "2024-06-01",
       "endDate": "2024-08-01"
     }
     ```

5. **Cancelar una reserva**
   - **Método**: `DELETE`
   - **URL**: `/reservations/{reservationId}`
   - **Descripción**: Cancela una reserva específica por su ID. Los usuarios solo pueden cancelar sus propias reservas. Los administradores pueden cancelar cualquier reserva.
   - **Autenticación**: Requiere autenticación.
   - **Roles Permitidos**: Todos los usuarios autenticados (limitado a sus propias reservas), Administrador (`ROLE_ADMIN`).

### Ejemplos de Uso

1. **Obtener todas las reservas (solo admin)**:

   ```http
   GET /reservations/admin
   ```

2. **Obtener historial de reservas del usuario autenticado**:

   ```http
   GET /reservations/my
   ```

3. **Obtener una reserva por ID**:

   ```http
   GET /reservations/{reservationId}
   ```

4. **Crear una nueva reserva**:

   ```http
   POST /reservations
   Content-Type: application/json

   {
       "productId": 1,
       "startDate": "2024-06-01",
       "endDate": "2024-08-01"
   }
   ```

5. **Cancelar una reserva**:
   ```http
   DELETE /reservations/{reservationId}
   ```

### Consideraciones

- **Autenticación**: Todos los endpoints requieren autenticación. El token JWT debe ser enviado en el encabezado `Authorization` como `Bearer <token>`.
- **Roles y Permisos**:
  - Los usuarios con rol `ROLE_USER` pueden crear, obtener y cancelar sus propias reservas.
  - Los usuarios con rol `ROLE_ADMIN` pueden obtener todas las reservas, así como crear (para su usuario solamente) y cancelar cualquier reserva.
- **Validaciones**:
  - La fecha de inicio no puede ser una fecha pasada.
  - La fecha de finalización no puede ser anterior a la fecha de inicio.
