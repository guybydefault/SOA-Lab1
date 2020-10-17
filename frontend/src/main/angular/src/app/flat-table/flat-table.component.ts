import {Component, Input, OnInit} from '@angular/core';
import {Flat} from "../domain/flat";

@Component({
  selector: 'app-flat-table',
  templateUrl: './flat-table.component.html',
  styleUrls: ['./flat-table.component.css']
})
export class FlatTableComponent implements OnInit {


  @Input() flats: Flat[]

  constructor() {
  }

  ngOnInit(): void {
  }


}
