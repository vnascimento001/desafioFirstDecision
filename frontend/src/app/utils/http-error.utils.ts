import { HttpErrorResponse } from "@angular/common/http";
import { AbstractControl } from "@angular/forms";

interface ApiErrorResponse {
  mensagem?: string;
  detalhes?: Record<string, string>;
}

const isApiErrorResponse = (payload: unknown): payload is ApiErrorResponse => {
  return typeof payload === "object" && payload !== null;
};

const hasDetalhes = (payload: ApiErrorResponse): payload is ApiErrorResponse & { detalhes: Record<string, string> } => {
  return typeof payload.detalhes === "object" && payload.detalhes !== null;
};

export const getApiErrorMessage = (erro: HttpErrorResponse, fallback: string): string => {
  if (isApiErrorResponse(erro.error) && typeof erro.error.mensagem === "string" && erro.error.mensagem.trim()) {
    return erro.error.mensagem;
  }

  return fallback;
};

export const applyBackendValidationErrors = (form: AbstractControl, erro: HttpErrorResponse): void => {
  if (!isApiErrorResponse(erro.error) || !hasDetalhes(erro.error)) {
    return;
  }

  Object.entries(erro.error.detalhes).forEach(([campo, mensagem]) => {
    const control = form.get(campo);
    if (!control) {
      return;
    }

    control.setErrors({ ...(control.errors || {}), backend: mensagem });
    control.markAsTouched();
  });
};
