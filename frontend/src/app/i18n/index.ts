import { EN_US_LABELS } from "./en-us";
import { PT_BR_LABELS } from "./pt-br";
import { AppLabels } from "./types";
import { APP_CONFIG } from "../config/app-config";

export type AppLocale = "pt-BR" | "en-US";

const labelsByLocale: Record<AppLocale, AppLabels> = {
  "pt-BR": PT_BR_LABELS,
  "en-US": EN_US_LABELS
};

export const DEFAULT_LOCALE: AppLocale = APP_CONFIG.i18n.defaultLocale as AppLocale;

export function getLabels(locale: AppLocale = DEFAULT_LOCALE): AppLabels {
  return labelsByLocale[locale];
}

export const LABELS = getLabels(DEFAULT_LOCALE);
