import {EventExpansionDetailsDto} from '../event/event-expansion-details.dto';

export class LocationEventDto {
  constructor(
    public name: string,
    public street: string,
    public postalCode: number,
    public town: string,
    public country: string,
    public imageReference: string,
    public events: EventExpansionDetailsDto[]
  ) {
  }
}
