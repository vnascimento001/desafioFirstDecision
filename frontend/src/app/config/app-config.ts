import { environment } from "../../environments/environment";

export const APP_CONFIG = {
  api: {
    baseUrl: environment.apiBaseUrl,
    usuariosPath: "/api/usuarios"
  },
  i18n: {
    defaultLocale: environment.defaultLocale
  }
} as const;
