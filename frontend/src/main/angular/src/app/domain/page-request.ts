export class PageRequest {
  pageIndex: number;
  size: number;


  constructor(pageIndex: number, size: number) {
    this.pageIndex = pageIndex;
    this.size = size;
  }
}
