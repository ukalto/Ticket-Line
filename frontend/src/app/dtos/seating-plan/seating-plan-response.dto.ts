export enum SectorType {
  standing = 'standing',
  seating = 'seating',
}

export class SeatingPlanResponse {
  id: number;
  name: string;
  locatedIn: number;
  capacity: number;
  sectors: Sector[];
}

export class Sector {
  id: number;
  name: string;
  color: string;
  type: SectorType;
  rows: Row[];
  capacity: number;
}

export class Row {
  seats: Seat[];
}

export class Seat {
  enabled: boolean;
}
