export class TicketDto {
    constructor(
        public readonly startingTime: Date,
        public readonly eventName: string,
        public readonly locationName: string,
        public readonly roomName: string,
        public readonly price: number,
        public readonly secret: string,
        public readonly printableSeatDescription: string
    ) {}
}
