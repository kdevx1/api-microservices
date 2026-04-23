import { DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, finalize } from 'rxjs';
import { UserService } from '../../../core/auth/services/user.service/user.service';

export class UserDataSource extends DataSource<any> {

  private usersSubject = new BehaviorSubject<any[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable(); // 👈 AQUI
  public total = 0;

  constructor(private service: UserService) {
    super();
  }

  connect() {
    return this.usersSubject.asObservable();
  }

  disconnect() {
    this.usersSubject.complete();
    this.loadingSubject.complete();
  }

  loadUsers(params: any) {
    this.loadingSubject.next(true); // 🚀 start loading

    this.service.getUsers(params)
      .pipe(
        finalize(() => this.loadingSubject.next(false)) // 🧠 stop loading
      )
      .subscribe(res => {
        this.usersSubject.next(res.content);
        this.total = res.totalElements;
      });
  }
}