import { CommonModule } from "@angular/common";
import { HttpErrorResponse } from "@angular/common/http";
import { Component } from "@angular/core";
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { MatCardModule } from "@angular/material/card";

import { RequestCadastroUsuario } from "../../models/usuario.models";
import { UsuarioService } from "../../services/usuario.service";
import { LABELS } from "../../i18n";
import { applyBackendValidationErrors, getApiErrorMessage } from "../../utils/http-error.utils";

const senhasIguaisValidator = (senhaCampo: string, confirmacaoCampo: string): ValidatorFn => {
  return (group: AbstractControl): ValidationErrors | null => {
    const senha = group.get(senhaCampo)?.value;
    const confirmacao = group.get(confirmacaoCampo)?.value;

    if (!senha || !confirmacao) {
      return null;
    }

    return senha === confirmacao ? null : { senhasDiferentes: true };
  };
};

@Component({
  selector: "app-usuario-cadastro",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatCardModule
  ],
  templateUrl: "./usuario-cadastro.component.html",
  styleUrl: "./usuario-cadastro.component.css"
})
export class UsuarioCadastroComponent {
  readonly labels = LABELS.cadastro;
  enviando = false;

  readonly formulario = this.formBuilder.group(
    {
      nome: ["", [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ["", [Validators.required, Validators.email]],
      senha: ["", [Validators.required, Validators.minLength(6), Validators.maxLength(20)]],
      confirmacaoSenha: ["", [Validators.required]]
    },
    {
      validators: [senhasIguaisValidator("senha", "confirmacaoSenha")]
    }
  );

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly usuarioService: UsuarioService,
    private readonly snackBar: MatSnackBar
  ) {}

  submeter(): void {
    if (this.formulario.invalid || this.enviando) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando = true;

    const payload = this.formulario.getRawValue() as RequestCadastroUsuario;

    this.usuarioService.cadastrar(payload).subscribe({
      next: () => {
        this.formulario.reset();
        this.enviando = false;
        this.snackBar.open(this.labels.sucessoCadastro, LABELS.listagem.fechar, { duration: 3000 });
      },
      error: (erro: HttpErrorResponse) => {
        this.enviando = false;
        applyBackendValidationErrors(this.formulario, erro);
        this.snackBar.open(getApiErrorMessage(erro, this.labels.falhaCadastro), LABELS.listagem.fechar, {
          duration: 4000
        });
      }
    });
  }

  campoInvalido(nomeCampo: "nome" | "email" | "senha" | "confirmacaoSenha"): boolean {
    const campo = this.formulario.get(nomeCampo);
    return !!campo && campo.invalid && (campo.touched || campo.dirty);
  }

}
