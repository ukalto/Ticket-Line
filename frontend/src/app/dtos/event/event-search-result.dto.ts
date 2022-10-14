export class EventSearchResultDto {
    constructor(
      public id: number,
      public title: string,
      public description: string,
      public categoryId: number,
      public soldOut: boolean,
      public hasShowingsInFuture: boolean,
      public imageFile: string,
      public durationHours: number,
      public durationMinutes: number
    ) {
    }
  }
