import { CommonModule } from "@angular/common";
import { HttpErrorResponse } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MatSortModule, Sort } from "@angular/material/sort";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

import { ResponseCadastroUsuario } from "../../models/usuario.models";
import { UsuarioService } from "../../services/usuario.service";
import { UsuarioEdicaoDialogComponent } from "./usuario-edicao-dialog.component";
import { LABELS } from "../../i18n";

@Component({
  selector: "app-usuario-listagem",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: "./usuario-listagem.component.html",
  styleUrl: "./usuario-listagem.component.css"
})
export class UsuarioListagemComponent implements OnInit {
  readonly labels = LABELS.listagem;
  usuarios: ResponseCadastroUsuario[] = [];
  colunas: string[] = ["nome", "email", "acoes"];
  carregando = false;
  mensagemErro = "";
  paginaAtual = 0;
  tamanhoPagina = 10;
  totalElementos = 0;
  busca = "";
  campoOrdenacao: "nome" | "email" = "nome";
  direcaoOrdenacao: "asc" | "desc" = "asc";

  constructor(
    private readonly usuarioService: UsuarioService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.carregarUsuarios(this.paginaAtual);
  }

  carregarUsuarios(pagina: number): void {
    this.carregando = true;
    this.mensagemErro = "";

    this.usuarioService
      .listar(pagina, this.tamanhoPagina, this.campoOrdenacao, this.direcaoOrdenacao, this.busca)
      .subscribe({
        next: (pagina) => {
          this.usuarios = pagina.content;
          this.paginaAtual = pagina.number;
          this.totalElementos = pagina.totalElements;
          this.carregando = false;
        },
        error: (erro: HttpErrorResponse) => {
          this.mensagemErro = erro.error?.mensagem || this.labels.falhaListagem;
          this.carregando = false;
        }
      });
  }

  editar(usuario: ResponseCadastroUsuario): void {
    const dialogRef = this.dialog.open(UsuarioEdicaoDialogComponent, {
      width: "520px",
      data: { usuario }
    });

    dialogRef.afterClosed().subscribe((edicaoRealizada: boolean | undefined) => {
      if (!edicaoRealizada) {
        return;
      }

      this.snackBar.open(this.labels.sucessoEdicao, this.labels.fechar, { duration: 3000 });
      this.carregarUsuarios(this.paginaAtual);
    });
  }

  deletar(id: number): void {
    const confirmouExclusao = window.confirm(this.labels.confirmacaoExclusao);
    if (!confirmouExclusao) {
      return;
    }

    this.usuarioService.deletar(id).subscribe({
      next: () => {
        this.snackBar.open(this.labels.sucessoExclusao, this.labels.fechar, { duration: 3000 });
        this.carregarUsuarios(this.paginaAtual);
      },
      error: (erro: HttpErrorResponse) => {
        this.mensagemErro = erro.error?.mensagem || this.labels.falhaExclusao;
      }
    });
  }

  mudouPaginacao(evento: PageEvent): void {
    this.tamanhoPagina = evento.pageSize;
    this.carregarUsuarios(evento.pageIndex);
  }

  mudouOrdenacao(sort: Sort): void {
    if (!sort.active || !sort.direction) {
      this.campoOrdenacao = "nome";
      this.direcaoOrdenacao = "asc";
      this.carregarUsuarios(0);
      return;
    }

    this.campoOrdenacao = sort.active as "nome" | "email";
    this.direcaoOrdenacao = sort.direction as "asc" | "desc";
    this.carregarUsuarios(0);
  }

  pesquisar(): void {
    this.carregarUsuarios(0);
  }

  limparBusca(): void {
    if (!this.busca) {
      return;
    }

    this.busca = "";
    this.carregarUsuarios(0);
  }
}
