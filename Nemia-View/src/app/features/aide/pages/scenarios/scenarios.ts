import { Component } from '@angular/core';
import { MarkdownViewerComponent } from '../markdown-viewer/markdown-viewer';

@Component({
  selector: 'app-scenarios',
  standalone: true,
  imports: [MarkdownViewerComponent],
  template: `
    <app-markdown-viewer
      filePath="/Docs/help/SCENARIOS_MISE_EN_SITUATION_nemia-hub.md"
    />
  `,
})
export class ScenariosComponent {}
