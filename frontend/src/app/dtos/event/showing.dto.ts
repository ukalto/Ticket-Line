import {Pricing} from './pricing.dto';

export class Showing {
  constructor(
    public occursOn: string,
    public performedAt: number,
    public pricings: Pricing[],
  ) {
  }
}
