import { Component, OnInit, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { marked } from 'marked';

@Component({
  selector: 'app-markdown-viewer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './markdown-viewer.html',
  styleUrl: './markdown-viewer.scss',
})
export class MarkdownViewerComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly sanitizer = inject(DomSanitizer);

  @Input({ required: true }) filePath!: string;

  content: SafeHtml = '';
  isLoading = true;
  errorMessage = '';

  async ngOnInit(): Promise<void> {
    this.http
      .get(this.filePath, { responseType: 'arraybuffer' })
      .subscribe({
        next: async (buffer) => {
          const decoder = new TextDecoder('utf-8');
          const markdown = decoder.decode(buffer);
          const result = marked.parse(markdown);
          const html = result instanceof Promise ? await result : result;
          this.content = this.sanitizer.bypassSecurityTrustHtml(html);
          this.isLoading = false;
        },
        error: () => {
          this.errorMessage = 'Impossible de charger le document.';
          this.isLoading = false;
        },
      });
  }
}
