// Copia la web de Pulso (en OneDrive) a www/ para empaquetarla en la app Android.
// No copia lo del servidor (api/, .env, .vercel) ni archivos de desarrollo.
import { cpSync, rmSync, existsSync } from 'node:fs';
import { join, basename } from 'node:path';
import { homedir } from 'node:os';

const ORIGEN = process.env.PULSO_WEB || join(homedir(), 'OneDrive', 'Datos adjuntos', 'Sesion local', 'pulso');
const DESTINO = new URL('../www/', import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, '$1');
const FUERA = new Set(['api', 'node_modules', '.vercel', '.vercelignore', '.gitignore', 'package.json', 'package-lock.json', 'README.md', 'sw.js', 'Pulso.apk', 'docs', 'LICENSE', '.git']);

if (!existsSync(ORIGEN)) { console.error('No encuentro la web en', ORIGEN); process.exit(1); }
rmSync(DESTINO, { recursive: true, force: true });
cpSync(ORIGEN, DESTINO, {
  recursive: true,
  filter: (src) => { const n = basename(src); return !FUERA.has(n) && !n.startsWith('.env'); },
});
console.log('Web copiada:', ORIGEN, '→', DESTINO);
