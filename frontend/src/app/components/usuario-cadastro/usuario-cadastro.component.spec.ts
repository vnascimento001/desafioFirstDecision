import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { of } from "rxjs";

import { UsuarioCadastroComponent } from "./usuario-cadastro.component";
import { UsuarioService } from "../../services/usuario.service";

describe("UsuarioCadastroComponent", () => {
  let component: UsuarioCadastroComponent;
  let fixture: ComponentFixture<UsuarioCadastroComponent>;
  let usuarioServiceSpy: jasmine.SpyObj<UsuarioService>;

  beforeEach(async () => {
    usuarioServiceSpy = jasmine.createSpyObj<UsuarioService>("UsuarioService", ["cadastrar"]);
    usuarioServiceSpy.cadastrar.and.returnValue(of({ id: 1, nome: "Ana", email: "ana@email.com" }));

    await TestBed.configureTestingModule({
      imports: [UsuarioCadastroComponent, NoopAnimationsModule],
      providers: [{ provide: UsuarioService, useValue: usuarioServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(UsuarioCadastroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("deve manter botao desabilitado quando formulario for invalido", () => {
    component.formulario.setValue({
      nome: "An",
      email: "email-invalido",
      senha: "123",
      confirmacaoSenha: "456"
    });

    expect(component.formulario.invalid).toBeTrue();
  });

  it("deve enviar cadastro quando formulario for valido", () => {
    component.formulario.setValue({
      nome: "Ana Maria",
      email: "ana@email.com",
      senha: "senha123",
      confirmacaoSenha: "senha123"
    });

    component.submeter();

    expect(usuarioServiceSpy.cadastrar).toHaveBeenCalledTimes(1);
    expect(component.enviando).toBeFalse();
  });
});
