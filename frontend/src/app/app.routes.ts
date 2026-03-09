import { Routes } from "@angular/router";

import { UsuarioCadastroComponent } from "./components/usuario-cadastro/usuario-cadastro.component";
import { UsuarioListagemComponent } from "./components/usuario-listagem/usuario-listagem.component";
import { APP_ROUTES } from "./constants/routes.constants";

export const appRoutes: Routes = [
  { path: APP_ROUTES.homePath, redirectTo: APP_ROUTES.cadastroPath, pathMatch: "full" },
  { path: APP_ROUTES.cadastroPath, component: UsuarioCadastroComponent },
  { path: APP_ROUTES.usuariosPath, component: UsuarioListagemComponent },
  { path: "**", redirectTo: APP_ROUTES.cadastroPath }
];
