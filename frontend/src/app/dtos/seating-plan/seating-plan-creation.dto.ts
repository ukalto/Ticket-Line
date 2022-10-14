export enum SectorType {
  standing = 'standing',
  seating = 'seating',
}

export class SeatingPlan {
  name: string;
  locatedIn: number;
  capacity: number;
  sectors: Sector[];
}

export class Sector {
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
