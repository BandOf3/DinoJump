Створення головного екрану-меню та імпорт зображень
===================

- Для створення нашого додатку необхідно завантажити картинки для створення наших ігрових одиниць та середовища відповідно
  Для цього перейдіть за [посиланням](https://drive.google.com/open?id=0B6Ffr-mG43I0QUVaTEI2cF9RU3M) і завантажте архів з папкою drawable і замініть нею відповідну папку в проекті

- Оновивши ресурси, переходимо безпосередньо до створення самого меню.
  Вікдриваємо ресурсний файл activity_menu.xml і додамо туди кнопки та background

  ```
  <?xml version="1.0" encoding="utf-8"?>
  <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:background="@drawable/splash"
      tools:context=".MainActivity">

      <Button
          android:id="@+id/play_button"
          android:text="Play Now"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:layout_above="@+id/buttonScore"
          android:layout_centerHorizontal="true" />

      <Button
          android:id="@+id/buttonScore"
          android:text="High Score"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:layout_alignParentBottom="true"
          android:layout_centerHorizontal="true" />

  </RelativeLayout>
  ```

- Далі переходимо до файлу MenuActivity.java

 ```
 public class MenuActivity extends AppCompatActivity {

     private Button mPlayButton;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_menu);
         setupView();
     }

     private void setupView() {

         mPlayButton = (Button) findViewById(R.id.play_button);
         mPlayButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(getBaseContext(),"перехід до ігрового екрану", Toast.LENGTH_SHORT).show();
             }
         });
     }

 }
 ```

- Запускаємо нашу програму і бачимо віповідне тост-повідомлення при натисканні на кнопку Play

- Тепер створюємо нове Activity і називаємо його GameActivity (нижче наведені скрін-шоти)

<img src="resources/task1/1.png"/>

<img src="resources/task1/2.png"/>

- Змінимо код файлу MenuActivity.java, а саме - обробник натиску клавіші Play

```
 public class MenuActivity extends AppCompatActivity {

     // Можемо прибрати оголошення змінної-кнопки, оскільки викликається лише один при обробці натискання

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_menu);
         setupView();
     }

     private void setupView() {
         findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) { startActivity(new Intent(MainActivity.this, GameActivity.class));}
         });
     }

 }
 ```

 - Запустимо програму і бачимо здійснений на новий, щоправда, білий екран. Тому задля уникнення цього переходимо до наступного кроку - створення View для нашого екрану
   (Для програм андроїд Activity (екран) супроводжується віповідним View,
   котрий може бути визначений на рівні інііцаіалізації Activity, тобто, xml файл, або при визначенні об'єкту типу View) (відповідна строка в конструкторі кожного Activity)
   ```
   setContentView(customView); // Власноруч визначений потомок View
   setContentView(R.id.activity_name); // відповідний xml файл з папки  resources/layout
   ```

- Для створення нашої гри ми будемо оголошувати власноруч View, а саме типу [SurfaceView](https://developer.android.com/reference/android/view/SurfaceView.html)
  Усі view "малюються" в тому ж потоці GUI, який також використовується для взаємодії із користувачем. Тож якщо вам необхідно оновити GUI швидко
  або ж рендеринг надто затратний і має суттєвий вплив на взаємодію із користувачем - сміливо використовуємо SurfaceView.

- Створюємо новий Java-class GameView, який наслідується від SurfaceView і також реалізовує інтерфейс Runnable

  ```
  public class GameView extends SurfaceView implements Runnable {

      //boolean variable to track if the game is playing or not
      volatile boolean playing;

      //the game thread
      private Thread gameThread = null;


      //Class constructor
      public GameView(Context context) {
          super(context);

      }

      @Override
      public void run() {
          while (playing) {
              //to update the frame
              update();

              //to draw the frame
              draw();

              //to control
              control();
          }
      }


      private void update() {

      }

      private void draw() {

      }

      private void control() {
          try {
              gameThread.sleep(17);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }

      public void pause() {
          //when the game is paused
          //setting the variable to false
          playing = false;
          try {
              //stopping the thread
              gameThread.join();
          } catch (InterruptedException e) {
          }
      }

      public void resume() {
          //when the game is resumed
          //starting the thread again
          playing = true;
          gameThread = new Thread(this);
          gameThread.start();
      }
  }
  ```

  - Після цього в нашому GameActivity змінимо ресурс контенту на новостворений і можемо видалити
    вже непотріний xml- ресурс

    ```
    public class GameActivity extends AppCompatActivity {

        private GameView mGameView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init(this);
        }

        public void init(Context context) {
            mGameView = new GameView(context);
            setContentView(mGameView);
        }

        @Override
        protected void onPause() {
            super.onPause();
            mGameView.pause();
        }

        @Override
        protected void onResume() {
            super.onResume();
            mGameView.resume();
        }
    }
    ```

    - Але чи не забули ми ще дещо важливе? Звісно ж, ресурси для зображень ігрових об'єктів!

    ```
    public class GameView extends SurfaceView implements Runnable {

        volatile boolean mPlaying;
        private Thread mGameThread = null;

        //general resource for all entities
        private Bitmap mGeneralImageResource;

        public GameView(Context context, Bitmap resource) {
            super(context);
            this.mGeneralImageResource = resource;

        }

        @Override
        public void run() {
            while (mPlaying) {
                update();
                draw();
                control();
            }
        }

        private void update() {
        }

        private void draw() {
        }

        private void control() {
            try {
                mGameThread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void pause() {
            mPlaying = false;
            try {
                mGameThread.join();
            } catch (InterruptedException e) {
            }
        }

        public void resume() {
            mPlaying = true;
            mGameThread = new Thread(this);
            mGameThread.start();
        }
    }
    ```

    - І не забуваємо про конвенцію Android-розробників, як  рекомендує називати змінні, починаючи змінні
    з англійської літери "m", але це не так важливо і можете сміливо їх оголошувати, як ваша душа забажає

    - Запускаємо код і знову бачимо вже не білий, а темний екран :)
     І на цьому поки що все, можете додатково прочитати про структуру Bitmap, SurfaceView, і конвенцію Android-розробки,
     в наступному уроці будемо створювати реальнні ігрові обєкти та симулювати рух поверхні

