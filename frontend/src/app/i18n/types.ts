export interface AppLabels {
  app: {
    brandTitle: string;
    brandSubtitle: string;
    menuCadastro: string;
    menuListagem: string;
  };
  cadastro: {
    titulo: string;
    nome: string;
    email: string;
    senha: string;
    confirmacaoSenha: string;
    botaoCadastrar: string;
    erroNomeObrigatorio: string;
    erroNomeMinimo: string;
    erroNomeMaximo: string;
    erroEmailObrigatorio: string;
    erroEmailInvalido: string;
    erroSenhaObrigatoria: string;
    erroSenhaMinima: string;
    erroSenhaMaxima: string;
    erroConfirmacaoObrigatoria: string;
    erroConfirmacaoDiferente: string;
    sucessoCadastro: string;
    falhaCadastro: string;
  };
  listagem: {
    titulo: string;
    buscaLabel: string;
    buscaPlaceholder: string;
    botaoPesquisar: string;
    botaoLimparBusca: string;
    colunaNome: string;
    colunaEmail: string;
    colunaAcoes: string;
    botaoEditar: string;
    botaoDeletar: string;
    nenhumCadastro: string;
    confirmacaoExclusao: string;
    sucessoEdicao: string;
    falhaEdicao: string;
    sucessoExclusao: string;
    falhaExclusao: string;
    falhaListagem: string;
    fechar: string;
  };
  edicao: {
    tituloModal: string;
    botaoCancelar: string;
    botaoSalvar: string;
    erroConfirmacaoDiferente: string;
  };
}
