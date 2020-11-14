export interface Flat {
  id: number;
  name: string;
  creationDate: Date;
  area: number;
  numberOfRooms: number;
  furnish: string;
  view: string;
  transport: string;
  coordinates: Coordinates;
  house: House;
}

export interface Coordinates {
  x: number;
  y: number;
}

export interface House {
  name: string;
  year: number;
  numberOfFloors: number;
  numberOfFlatsOnFloor: number;
  numberOfLifts: number;
}

export const FURNISH_TYPES = ['NONE', 'BAD', 'LITTLE'];
export const VIEW_TYPES = ['PARK', 'NORMAL', 'GOOD'];
export const TRANSPORT_TYPES = ['FEW', 'LITTLE', 'ENOUGH'];

export class Field {
  constructor(public property: string, public name: string) {
  }
}

export const FLAT_FIELDS = [
  new Field('id', 'Id'),
  new Field('name', 'Name'),
  new Field('creationDate', 'Creation date'),
  new Field('area', 'Area'),
  new Field('numberOfRooms', 'Number of rooms'),
  new Field('furnish', 'Furnish'),
  new Field('view', 'View'),
  new Field('transport', 'Transport'),
  new Field('coordinates.x', 'X'),
  new Field('coordinates.y', 'Y'),
  new Field('house.name', 'House name'),
  new Field('house.year', 'House year'),
  new Field('house.numberOfFloors', 'Floors number'),
  new Field('house.numberOfFlatsOnFloor', 'Flats on floor'),
  new Field('house.numberOfLifts', 'Lifts')
]

export class ComparisonOperation {
  constructor(public operation: string, public name: string) {
  }
}

export const comparisonOperations = [
  new ComparisonOperation("GT", "Greater than"),
  new ComparisonOperation("LT", "Less than"),
  new ComparisonOperation("GTE", "Greater than or equal to"),
  new ComparisonOperation("LTE", "Less than or equal to"),
  new ComparisonOperation("EQUAL", "Equal to")
]

export interface SearchCriteria {
  field: Field;
  operation: ComparisonOperation;
  value: any;
}
