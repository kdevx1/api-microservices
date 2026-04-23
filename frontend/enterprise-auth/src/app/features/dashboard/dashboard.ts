import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  standalone: true,
  imports: [ MatCardModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard {}
