export enum Role {
  ADMIN='ADMIN', 
  MANAGER='MANAGER',
  USER='USER'
  
}

export enum Permission {
  READ = 'READ',
  WRITE = 'WRITE',
  DELETE = 'DELETE',
  ALTER = 'ALTER',
  DASHBOARD_VIEW = 'DASHBOARD_VIEW' // 👈 aqui nasce
}
