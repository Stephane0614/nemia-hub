import { Component } from '@angular/core';
import { MarkdownViewerComponent } from '../markdown-viewer/markdown-viewer';

@Component({
  selector: 'app-guide',
  standalone: true,
  imports: [MarkdownViewerComponent],
  template: `
    <app-markdown-viewer
      filePath="/Docs/help/GUIDE_UTILISATEUR_nemia-hub.md"
    />
  `,
})
export class GuideComponent {}
