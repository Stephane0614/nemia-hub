import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatIcon],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
