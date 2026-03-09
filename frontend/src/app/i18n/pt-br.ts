import { AppLabels } from "./types";

export const PT_BR_LABELS: AppLabels = {
  app: {
    brandTitle: "Desafio First Decision",
    brandSubtitle: "CRUD de cadastros",
    menuCadastro: "Cadastro",
    menuListagem: "Listagem"
  },
  cadastro: {
    titulo: "Novo cadastro",
    nome: "Nome",
    email: "E-mail",
    senha: "Senha",
    confirmacaoSenha: "Confirmacao de senha",
    botaoCadastrar: "Cadastrar",
    erroNomeObrigatorio: "Nome e obrigatorio.",
    erroNomeMinimo: "Nome deve ter no minimo 3 caracteres.",
    erroNomeMaximo: "Nome deve ter no maximo 50 caracteres.",
    erroEmailObrigatorio: "E-mail e obrigatorio.",
    erroEmailInvalido: "E-mail invalido.",
    erroSenhaObrigatoria: "Senha e obrigatoria.",
    erroSenhaMinima: "Senha deve ter no minimo 6 caracteres.",
    erroSenhaMaxima: "Senha deve ter no maximo 20 caracteres.",
    erroConfirmacaoObrigatoria: "Confirmacao de senha e obrigatoria.",
    erroConfirmacaoDiferente: "Confirmacao de senha deve coincidir com a senha.",
    sucessoCadastro: "Usuario cadastrado com sucesso.",
    falhaCadastro: "Falha ao cadastrar usuario."
  },
  listagem: {
    titulo: "Usuarios cadastrados",
    buscaLabel: "Buscar",
    buscaPlaceholder: "Nome ou e-mail",
    botaoPesquisar: "Pesquisar",
    botaoLimparBusca: "Limpar",
    colunaNome: "Nome",
    colunaEmail: "E-mail",
    colunaAcoes: "Acoes",
    botaoEditar: "Editar",
    botaoDeletar: "Deletar",
    nenhumCadastro: "Nenhum cadastro encontrado.",
    confirmacaoExclusao: "Tem certeza que deseja excluir este usuario?",
    sucessoEdicao: "Usuario atualizado com sucesso.",
    falhaEdicao: "Falha ao editar usuario.",
    sucessoExclusao: "Usuario excluido com sucesso.",
    falhaExclusao: "Falha ao deletar usuario.",
    falhaListagem: "Falha ao carregar usuarios.",
    fechar: "Fechar"
  },
  edicao: {
    tituloModal: "Editar usuario",
    botaoCancelar: "Cancelar",
    botaoSalvar: "Salvar",
    erroConfirmacaoDiferente: "Confirmacao de senha deve coincidir com a senha."
  }
};
