# Pulso · App Android

App nativa de Android hecha con **Capacitor 8**. Empaqueta la web de Pulso (que vive en
`OneDrive/Datos adjuntos/Sesion local/pulso`) y le suma cosas que una web no puede hacer.

> Esta carpeta está **fuera de OneDrive** a propósito: OneDrive bloquea archivos mientras Gradle compila.

## Qué hace

- Pulso completo **sin internet** (la web va adentro del APK).
- **Sincronización** con la compu y la web (misma base de datos cifrada).
- **Avisos de vencimientos**: un día antes y el mismo día, con botón **“Ya pagué”**.
  Mi Fitness los reenvía a la **Xiaomi Smart Band 10 Pro** con vibración.
- **Carga automática de pagos**: `PulsoNotificationService` lee los avisos de Brubank, Mercado Pago, Ualá y otros bancos
  (solo esas apps) y los deja en una cola local. La web los toma con el plugin `PulsoNotif` y anota el gasto o ingreso solo.
  Se activa en Ajustes → **Cargar pagos solos** (permiso “Acceso a notificaciones” de Android).
- Ícono, pantalla de inicio y nombre propios (`ar.pulso.finanzas`).

### Probar la carga automática en el emulador

```bash
adb shell cmd notification allow_listener ar.pulso.finanzas/.PulsoNotificationService
adb shell "cmd notification post -S bigtext -t Brubank prueba 'Pagaste $ 4.500 en Starbucks'"
```

(En el APK de prueba también se leen los avisos de `com.android.shell`, para poder probar con adb.)

## Comandos

```powershell
npm run web     # copia la web de OneDrive a www/
npm run sync    # copia la web y actualiza el proyecto Android
npm run apk     # copia, sincroniza y compila el APK de prueba
npm run abrir   # abre el proyecto en Android Studio
```

El APK queda en `android/app/build/outputs/apk/debug/app-debug.apk`.

## Instalar en el celular

1. Pasá `app-debug.apk` al celular (cable, Google Drive o WhatsApp a vos mismo).
2. Abrilo y aceptá **Instalar apps desconocidas** para esa app (solo esta vez).
3. Abrí Pulso → Ajustes → **Probar aviso**. Si la banda está conectada a Mi Fitness, vibra.

Para que el aviso llegue a la banda: Mi Fitness → tu banda → **Notificaciones de apps** → activar **Pulso**.

## Hoja de ruta

| Fase | Qué | Estado |
|---|---|---|
| 1 | App Android con sincronización y avisos | ✅ código listo · compilar |
| 2 | **Carga automática** de avisos de bancos y billeteras | ✅ listo |
| 3a | Verificar si Mi Fitness deja instalar apps propias (`.rpk`) en la Band 10 Pro | prueba en tu banda |
| 3b | App **Pulso en la banda** (Vela JS quick app) con la interfaz de la simulación | después de 3a |
| 3c | Puente banda ↔ celular (Xiaomi Interconnect) para guardar el gasto en Pulso | después de 3b |
| 4 | APK firmada de producción (y opcional Play Store) | al final |
