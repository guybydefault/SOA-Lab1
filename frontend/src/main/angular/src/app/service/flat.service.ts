import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Flat} from "../domain/flat";
import {Pageable} from "../domain/pageable";


const baseUrl = 'http://localhost:8080/Lab1_Server/api/flats';

@Injectable({
  providedIn: 'root'
})
export class FlatService {


  constructor(private http: HttpClient) {
  }

  findFlats(pageRequest, sort, filter) {
    return this.http.get<Pageable<Flat>>(`${baseUrl}`)
  }

  deleteFlat() {
    // this.http.delete()
  }

  updateFlat() {
    // this.http.put()
  }

  saveFlat() {
    // this.http.post()
  }

}
