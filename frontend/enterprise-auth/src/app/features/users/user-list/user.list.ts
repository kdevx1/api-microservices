import { Component, ViewChild, AfterViewInit, inject } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { merge, startWith, switchMap } from 'rxjs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserService } from '../../../core/auth/services/user.service/user.service';
import { UserDataSource } from '../../users/user-datasource/user.datasource';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  standalone: true,
  templateUrl: './user.list.html',
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginator,
    MatSort,
    MatIconModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
        
  ]
})
export class UserList implements AfterViewInit {

  private service = inject(UserService);

  dataSource = new UserDataSource(this.service);

  displayedColumns = ['name', 'email', 'role', 'actions'];

  total = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filter = '';

  ngAfterViewInit() {

    merge(this.paginator.page, this.sort.sortChange)
      .pipe(
        startWith({}),
        switchMap(() => {

          const params = {
            page: this.paginator.pageIndex,
            size: this.paginator.pageSize,
            sort: this.sort.active
              ? `${this.sort.active},${this.sort.direction}`
              : '',
            name: this.filter
          };

          this.dataSource.loadUsers(params);

          return [];
        })
      )
      .subscribe(() => {
        this.total = this.dataSource.total;
      });
  }
}