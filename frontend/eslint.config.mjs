import globals from "globals";
import tseslint from "typescript-eslint";

export default [
  { files: ["**/*.{ts,tsx}"] },
  { ignores: ["dist/"] },
  {
    languageOptions: {
      globals: { ...globals.browser, ...globals.es2020 },
    },
    rules: {
      "no-unused-vars": "off",
      "@typescript-eslint/no-unused-vars": ["warn", { argsIgnorePattern: "^_" }],
    },
  },
  ...tseslint.configs.recommended,
];
