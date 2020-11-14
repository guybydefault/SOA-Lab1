import {Injectable, TemplateRef} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  constructor() {
  }

  toasts: any[] = [];

  show(header: string, textOrTpl: string | TemplateRef<any>, options: any = {}) {
    this.toasts.push({header, textOrTpl, ...options});
  }

  remove(toast) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }

  showInfo(msg: string) {
    this.show('Info', msg, {delay: 1250});
  }

  showSuccess(msg: string) {
    this.show('Success!', msg, {classname: 'bg-success text-light', delay: 1250});
  }

  showError(msg: string) {
    this.show('Error!', msg, {classname: 'bg-danger text-light', delay: 1250});
  }

}
