import { Line } from '../classes/line';
import { RIDES1_DATA } from './rides_data';
import { RIDES2_DATA } from './rides_data';
import { RIDES3_DATA } from './rides_data';

export const LINES_DATA: Line[] = [
    {
        lineName: 'Linea1',
        rides: RIDES1_DATA
    },
    {
        lineName: 'Linea2',
        rides: RIDES2_DATA
    },
    {
        lineName: 'Linea3',
        rides: RIDES3_DATA
    }
  ];
