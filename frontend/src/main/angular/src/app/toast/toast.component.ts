import {Component, HostBinding, OnInit, TemplateRef} from '@angular/core';
import {ToastService} from "../service/toast.service";

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css']
})
export class ToastComponent implements OnInit {
  @HostBinding('class.ngb-toasts') binding = true;

  constructor(public toastService: ToastService) {
  }

  ngOnInit(): void {
  }

  isTemplate(toast) { return toast.textOrTpl instanceof TemplateRef; }
}
