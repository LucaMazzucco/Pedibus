import { BrowserModule } from '@angular/platform-browser';
import {Input, NgModule} from '@angular/core';
import { AppComponent } from './app.component';
import { MatListModule } from '@angular/material/list';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { LayoutModule } from '@angular/cdk/layout';
import {MatToolbarModule, MatButtonModule, MatSidenavModule, MatTableModule} from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AttendanceComponent } from './attendance/attendance.component'
import { MatCardModule } from '@angular/material';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTabsModule } from '@angular/material/tabs';
import { MatMenuModule } from '@angular/material/menu';
import { RegisterComponent } from './register/register.component'
import {MatSelectModule} from '@angular/material/select';
import { LoginComponent } from './login/login.component';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatInputModule} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {JwtInterceptor} from './services/jwt-interceptor';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatDialogModule} from '@angular/material/dialog';
import {MatCheckboxModule} from "@angular/material";
import {AuthguardService as AuthGuard} from "./services/authguard.service";
import { BookingComponent } from './booking/booking.component';
import {MatBadgeModule} from '@angular/material/badge';
import { MessagesComponent } from './messages/messages.component';
import { AvailabilityComponent } from './availability/availability.component';
//import { AdministrationComponent } from './administration/administration.component';

const routes: Routes = [
  { path: 'presenze',
    component: AttendanceComponent,
    canActivate:[AuthGuard]
  },
  { path: 'login',
    component: LoginComponent},
  { path: 'prenotazioni',
    component: BookingComponent
  },
  { path: 'register', component: RegisterComponent},
  { path: 'disponibilita', component: AvailabilityComponent},
  { path: 'messaggi', component: MessagesComponent},
  { path: '', component: LoginComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent,
    AttendanceComponent,
    LoginComponent,
    RegisterComponent,
    BookingComponent,
    MessagesComponent,
    AvailabilityComponent
  ],
  imports: [
    BrowserModule,
    MatTableModule,
    HttpClientModule,
    MatCheckboxModule,
    MatSelectModule,
    BrowserAnimationsModule,
    MatListModule,
    MatInputModule,
    ReactiveFormsModule,
    MatIconModule,
    MatExpansionModule,
    LayoutModule,
    MatBadgeModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSidenavModule,
    FlexLayoutModule,
    MatCardModule,
    MatPaginatorModule,
    MatTabsModule,
    MatMenuModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatSnackBarModule,
    MatDialogModule,
    RouterModule.forRoot(
      routes,
      { enableTracing: false } // <-- debugging purposes only
    )
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
