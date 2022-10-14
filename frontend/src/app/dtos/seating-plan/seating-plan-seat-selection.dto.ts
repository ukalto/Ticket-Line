export enum SectorType {
  standing = 'standing',
  seating = 'seating',
}

// main data structure
export class SeatingPlan {
  sectors: Sector[];
}

export class Sector {
  name: string;
  color: string;
  type: SectorType;
  vacancies?: number;
  rows: Row[];
  capacity: number;
  isSelected: boolean;
}

export class Row {
  seats: Seat[];
}

export class Seat {
  alreadyBooked: boolean;
  visible: boolean;
  isSelected: boolean;
}
