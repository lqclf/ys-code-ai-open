declare module 'markdown-it-sanitizer' {
  import MarkdownIt from 'markdown-it'

  interface MarkdownItSanitizerOptions {
    allowedTags?: string[]
    allowedAttributes?: Record<string, string[]>
    allowedClasses?: Record<string, string[]>
    allowedStyles?: Record<string, string[]>
    allowedSchemes?: string[]
    allowedSchemesAppliedToAttributes?: string[]
  }

  interface MarkdownItSanitizerPlugin {
    (md: MarkdownIt, options?: MarkdownItSanitizerOptions): void
  }

  const markdownItSanitizer: MarkdownItSanitizerPlugin

  export = markdownItSanitizer
}
