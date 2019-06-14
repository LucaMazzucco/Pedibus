import { Ride } from '../classes/ride';
import { RIDE1_STOPS1_DATA } from './stops_data';
import { RIDE2_STOPS1_DATA } from './stops_data';
import { RIDE3_STOPS1_DATA } from './stops_data';
import { RIDE1_STOPS2_DATA } from './stops_data';
import { RIDE2_STOPS2_DATA } from './stops_data';
import { RIDE3_STOPS2_DATA } from './stops_data';
import { RIDE1_STOPS3_DATA } from './stops_data';
import { RIDE2_STOPS3_DATA } from './stops_data';
import { RIDE3_STOPS3_DATA } from './stops_data';
import { RIDE1_STOPS1_BACK_DATA } from './stops_data';
import { RIDE2_STOPS1_BACK_DATA } from './stops_data';
import { RIDE3_STOPS1_BACK_DATA } from './stops_data';
import { RIDE1_STOPS2_BACK_DATA } from './stops_data';
import { RIDE2_STOPS2_BACK_DATA } from './stops_data';
import { RIDE3_STOPS2_BACK_DATA } from './stops_data';
import { RIDE1_STOPS3_BACK_DATA } from './stops_data';
import { RIDE2_STOPS3_BACK_DATA } from './stops_data';
import { RIDE3_STOPS3_BACK_DATA } from './stops_data';

export const RIDES1_DATA: Ride[] = [
    {
        date: new Date(2019, 4, 26, 16, 52, 0),
        stops: RIDE1_STOPS1_DATA,
        stopsBack: RIDE1_STOPS1_BACK_DATA
    },
    {
        date: new Date(2019, 4, 30, 16, 52, 0),
        stops: RIDE2_STOPS1_DATA,
        stopsBack: RIDE2_STOPS1_BACK_DATA
    },
    {
        date: new Date(2019, 4, 28, 16, 52, 0),
        stops: RIDE3_STOPS1_DATA,
        stopsBack: RIDE3_STOPS1_BACK_DATA
    }
  ];

export const RIDES2_DATA: Ride[] = [
    {
        date: new Date(2019, 4, 26, 16, 52, 0),
        stops: RIDE1_STOPS2_DATA,
        stopsBack: RIDE1_STOPS2_BACK_DATA
    },
    {
        date: new Date(2019, 4, 28, 16, 52, 0),
        stops: RIDE2_STOPS2_DATA,
        stopsBack: RIDE2_STOPS2_BACK_DATA
    },
    {
        date: new Date(2019, 4, 30, 16, 52, 0),
        stops: RIDE3_STOPS2_DATA,
        stopsBack: RIDE3_STOPS2_BACK_DATA
    }
  ];

export const RIDES3_DATA: Ride[] = [
    {
        date: new Date(2019, 4, 26, 16, 52, 0),
        stops: RIDE1_STOPS3_DATA,
        stopsBack: RIDE1_STOPS3_BACK_DATA
    },
    {
        date: new Date(2019, 4, 28, 16, 52, 0),
        stops: RIDE2_STOPS3_DATA,
        stopsBack: RIDE2_STOPS3_BACK_DATA
    },
    {
        date: new Date(2019, 4, 30, 16, 52, 0),
        stops: RIDE3_STOPS3_DATA,
        stopsBack: RIDE3_STOPS3_BACK_DATA
    }
  ];
