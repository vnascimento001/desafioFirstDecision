import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { throwError } from "rxjs";
import { catchError } from "rxjs/operators";

interface ApiErrorPayload {
  mensagem?: string;
}

const temMensagem = (payload: unknown): payload is ApiErrorPayload => {
  return (
    typeof payload === "object" &&
    payload !== null &&
    "mensagem" in payload &&
    typeof (payload as { mensagem?: unknown }).mensagem === "string"
  );
};

const mensagemPorStatus = (status: number): string => {
  if (status === 0) {
    return "Nao foi possivel conectar ao servidor. Verifique sua conexao e tente novamente.";
  }
  if (status >= 500) {
    return "Erro interno no servidor. Tente novamente em instantes.";
  }
  if (status === 404) {
    return "Recurso nao encontrado.";
  }
  if (status === 401 || status === 403) {
    return "Voce nao tem permissao para realizar esta acao.";
  }
  if (status === 400) {
    return "Requisicao invalida.";
  }
  return "Falha na comunicacao com o servidor.";
};

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((erro: unknown) => {
      if (!(erro instanceof HttpErrorResponse)) {
        return throwError(() => erro);
      }

      if (temMensagem(erro.error)) {
        return throwError(() => erro);
      }

      const erroNormalizado = new HttpErrorResponse({
        error: { mensagem: mensagemPorStatus(erro.status) },
        headers: erro.headers,
        status: erro.status,
        statusText: erro.statusText,
        url: erro.url ?? undefined
      });

      return throwError(() => erroNormalizado);
    })
  );
};
