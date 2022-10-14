import {EventExpansionDetailsDto} from '../event/event-expansion-details.dto';

export class ArtistEventDto {
  constructor(
    public name: string,
    public events: EventExpansionDetailsDto[]
  ) {
  }
}
