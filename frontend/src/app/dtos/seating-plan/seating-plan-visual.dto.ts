export enum SectorType {
    standing = 'standing',
    seating = 'seating',
}

// main data structure
export class SeatingPlan {
    sectors: Sector[];
}

export class Sector {
    color: string;
    type: SectorType;
    vacancies?: number;
    rows: Row[];
    capacity: number;
    isSelected: boolean;
}

export class Row {
    seats: Seat[];
};

export class Seat {
    visible: boolean;
    grayedOut: boolean;
    isSelected: boolean;
}

// event data
export class NewSectorPosition {
    predecessor: number;
    successor: number;
}

export class SeatSpecifier {
    sector: number;
    row: number;
    seat: number;

    constructor(sector: number = undefined, row: number = undefined, seat: number = undefined) {
        this.sector = sector;
        this.row = row;
        this.seat = seat;
    }
}

export class NonSeatSpecifier {
    sector: number;
    amount: number; // amount of tickets

    constructor(sector: number = undefined, amount: number = undefined) {
        this.sector = sector;
        this.amount = amount;
    }
}
