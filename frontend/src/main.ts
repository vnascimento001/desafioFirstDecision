import "zone.js";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { provideRouter } from "@angular/router";
import { provideAnimations } from "@angular/platform-browser/animations";
import { bootstrapApplication } from "@angular/platform-browser";

import { AppComponent } from "./app/app.component";
import { appRoutes } from "./app/app.routes";
import { httpErrorInterceptor } from "./app/interceptors/http-error.interceptor";

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([httpErrorInterceptor])),
    provideRouter(appRoutes),
    provideAnimations()
  ]
}).catch((err) => console.error(err));
