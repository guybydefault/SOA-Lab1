import {Component, OnInit} from '@angular/core';
import {Flat} from "../domain/flat";
import {FlatService} from "../service/flat.service";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  flats: Flat[];

  constructor(private flatService: FlatService) {
  }

  ngOnInit(): void {
    this.reloadCities();
  }

  reloadCities() {
    this.flatService.findFlats(null, null, null).subscribe(res => {
      this.flats = res.content;
    });
  }

  sortUpdated(event) {

  }

  filterUpdated(event) {

  }

}
