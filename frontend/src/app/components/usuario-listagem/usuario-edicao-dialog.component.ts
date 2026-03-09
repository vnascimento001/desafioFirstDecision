import { CommonModule } from "@angular/common";
import { HttpErrorResponse } from "@angular/common/http";
import { Component, Inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";

import { RequestEdicaoUsuario, ResponseCadastroUsuario } from "../../models/usuario.models";
import { LABELS } from "../../i18n";
import { UsuarioService } from "../../services/usuario.service";
import { applyBackendValidationErrors, getApiErrorMessage } from "../../utils/http-error.utils";

interface DialogData {
  usuario: ResponseCadastroUsuario;
}

@Component({
  selector: "app-usuario-edicao-dialog",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: "./usuario-edicao-dialog.component.html",
  styleUrl: "./usuario-edicao-dialog.component.css"
})
export class UsuarioEdicaoDialogComponent {
  readonly labelsCadastro = LABELS.cadastro;
  readonly labelsEdicao = LABELS.edicao;
  readonly labelsListagem = LABELS.listagem;
  salvando = false;
  mensagemErro = "";

  readonly formularioEdicao = this.formBuilder.group({
    nome: [this.data.usuario.nome, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    email: [this.data.usuario.email, [Validators.required, Validators.email]],
    senha: ["", [Validators.required, Validators.minLength(6), Validators.maxLength(20)]],
    confirmacaoSenha: ["", [Validators.required]]
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) readonly data: DialogData,
    private readonly dialogRef: MatDialogRef<UsuarioEdicaoDialogComponent>,
    private readonly formBuilder: FormBuilder,
    private readonly usuarioService: UsuarioService
  ) {}

  salvar(): void {
    if (this.formularioEdicao.invalid || this.salvando) {
      this.formularioEdicao.markAllAsTouched();
      return;
    }

    const senha = this.formularioEdicao.get("senha")?.value;
    const confirmacaoSenha = this.formularioEdicao.get("confirmacaoSenha")?.value;
    if (senha !== confirmacaoSenha) {
      this.formularioEdicao
        .get("confirmacaoSenha")
        ?.setErrors({ senhasDiferentes: true, mensagem: this.labelsEdicao.erroConfirmacaoDiferente });
      return;
    }

    this.salvando = true;
    this.mensagemErro = "";

    const payload = this.formularioEdicao.getRawValue() as RequestEdicaoUsuario;
    this.usuarioService.editar(this.data.usuario.id, payload).subscribe({
      next: () => {
        this.salvando = false;
        this.dialogRef.close(true);
      },
      error: (erro: HttpErrorResponse) => {
        this.salvando = false;
        applyBackendValidationErrors(this.formularioEdicao, erro);
        this.mensagemErro = getApiErrorMessage(erro, this.labelsListagem.falhaEdicao);
      }
    });
  }

  cancelar(): void {
    this.dialogRef.close();
  }
}
