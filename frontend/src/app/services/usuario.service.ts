import { HttpClient } from "@angular/common/http";
import { HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

import {
  RequestCadastroUsuario,
  RequestEdicaoUsuario,
  ResponseCadastroUsuario,
  ResponseEdicaoUsuario,
  SpringPage
} from "../models/usuario.models";
import { APP_CONFIG } from "../config/app-config";

@Injectable({ providedIn: "root" })
export class UsuarioService {
  private readonly apiUrl = `${APP_CONFIG.api.baseUrl}${APP_CONFIG.api.usuariosPath}`;

  constructor(private readonly http: HttpClient) {}

  cadastrar(payload: RequestCadastroUsuario): Observable<ResponseCadastroUsuario> {
    return this.http.post<ResponseCadastroUsuario>(this.apiUrl, payload);
  }

  listar(
    page = 0,
    size = 10,
    sortField: "nome" | "email" = "nome",
    sortDirection: "asc" | "desc" = "asc",
    busca = ""
  ): Observable<SpringPage<ResponseCadastroUsuario>> {
    let params = new HttpParams()
      .set("page", page)
      .set("size", size)
      .set("sort", `${sortField},${sortDirection}`);

    const termoBusca = busca.trim();
    if (termoBusca) {
      params = params.set("busca", termoBusca);
    }

    return this.http.get<SpringPage<ResponseCadastroUsuario>>(this.apiUrl, { params });
  }

  editar(id: number, payload: RequestEdicaoUsuario): Observable<ResponseEdicaoUsuario> {
    return this.http.put<ResponseEdicaoUsuario>(`${this.apiUrl}/${id}`, payload);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
