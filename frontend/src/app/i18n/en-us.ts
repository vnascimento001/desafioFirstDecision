import { AppLabels } from "./types";

export const EN_US_LABELS: AppLabels = {
  app: {
    brandTitle: "First Decision",
    brandSubtitle: "User management",
    menuCadastro: "Register",
    menuListagem: "Users"
  },
  cadastro: {
    titulo: "New registration",
    nome: "Name",
    email: "Email",
    senha: "Password",
    confirmacaoSenha: "Confirm password",
    botaoCadastrar: "Register",
    erroNomeObrigatorio: "Name is required.",
    erroNomeMinimo: "Name must be at least 3 characters.",
    erroNomeMaximo: "Name must be at most 50 characters.",
    erroEmailObrigatorio: "Email is required.",
    erroEmailInvalido: "Invalid email.",
    erroSenhaObrigatoria: "Password is required.",
    erroSenhaMinima: "Password must be at least 6 characters.",
    erroSenhaMaxima: "Password must be at most 20 characters.",
    erroConfirmacaoObrigatoria: "Password confirmation is required.",
    erroConfirmacaoDiferente: "Password confirmation must match password.",
    sucessoCadastro: "User registered successfully.",
    falhaCadastro: "Failed to register user."
  },
  listagem: {
    titulo: "Registered users",
    buscaLabel: "Search",
    buscaPlaceholder: "Name or email",
    botaoPesquisar: "Search",
    botaoLimparBusca: "Clear",
    colunaNome: "Name",
    colunaEmail: "Email",
    colunaAcoes: "Actions",
    botaoEditar: "Edit",
    botaoDeletar: "Delete",
    nenhumCadastro: "No records found.",
    confirmacaoExclusao: "Are you sure you want to delete this user?",
    sucessoEdicao: "User updated successfully.",
    falhaEdicao: "Failed to update user.",
    sucessoExclusao: "User deleted successfully.",
    falhaExclusao: "Failed to delete user.",
    falhaListagem: "Failed to load users.",
    fechar: "Close"
  },
  edicao: {
    tituloModal: "Edit user",
    botaoCancelar: "Cancel",
    botaoSalvar: "Save",
    erroConfirmacaoDiferente: "Password confirmation must match password."
  }
};
