import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AngularOpenlayersModule } from "ngx-openlayers";
import {HttpClientModule} from "@angular/common/http";
import { MapComponent } from './map/map.component';
import { BlockUIModule } from 'ng-block-ui';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule, MatSnackBarModule} from "@angular/material";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    MapComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AngularOpenlayersModule,
    BlockUIModule.forRoot(),
    MatButtonModule,
    MatInputModule,
    BrowserAnimationsModule,
    FormsModule,
    MatSnackBarModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
