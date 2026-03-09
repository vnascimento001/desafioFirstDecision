export interface RequestCadastroUsuario {
  nome: string;
  email: string;
  senha: string;
  confirmacaoSenha: string;
}

export interface RequestEdicaoUsuario {
  nome: string;
  email: string;
  senha: string;
  confirmacaoSenha: string;
}

export interface ResponseCadastroUsuario {
  id: number;
  nome: string;
  email: string;
}

export interface ResponseEdicaoUsuario {
  id: number;
  nome: string;
  email: string;
}

export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
