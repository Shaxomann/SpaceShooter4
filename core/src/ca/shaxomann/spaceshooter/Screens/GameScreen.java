package ca.shaxomann.spaceshooter.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

import ca.shaxomann.spaceshooter.Factory.EnemyShip;
import ca.shaxomann.spaceshooter.Factory.EnemyTank;
import ca.shaxomann.spaceshooter.SpriteAndEffects.Explosion;
import ca.shaxomann.spaceshooter.SpriteAndEffects.Laser;
import ca.shaxomann.spaceshooter.Factory.PlayerShip;
import ca.shaxomann.spaceshooter.Factory.RapidFireBoss;
import ca.shaxomann.spaceshooter.SpaceShooterGame;
import ca.shaxomann.spaceshooter.SpriteAndEffects.Rocket;
import ca.shaxomann.spaceshooter.SpriteAndEffects.SparkEffect;
import ca.shaxomann.spaceshooter.SpriteAndEffects.WarpEffect;

public class GameScreen implements Screen {


    ///
    private Game game;
    public int score = 0;

    SpaceShooterGame spaceShooterGame;

    // next stage
    private GameScreen2 gameScreen2;

    //screen
    private final Camera camera;
    private final Viewport viewport;

    //graphics
    private final SpriteBatch batch;
    private final Texture explosionTexture;
    private Texture sparkTexture, warpTexture;

    private final TextureRegion[] backgrounds;
    private final float backgroundHeight; //height of background in World units

    private final TextureRegion enemyShipTextureRegion;
    private final TextureRegion enemyShieldTextureRegion;
    private final TextureRegion enemyLaserTextureRegion;
    private TextureRegion rapidFireBossLaserTextureRegion;
    private final TextureRegion enemyTankTextureRegion;

    private Texture healthBarTexture;

    // POWERS TEXTURE
    private TextureRegion powerShield;
    private TextureRegion powerUp;


    // NON ENNEMIES THREAT

    private TextureRegion littleComet, mediumComet, BigComet;

    // MUSIC
    Music ambiant = Gdx.audio.newMusic(Gdx.files.internal("Mayhem.mp3"));

    // SOUND EFFECTS

    Sound laserSoundPlayer = Gdx.audio.newSound(Gdx.files.internal("lasersound.wav"));
    Sound explosionSound = Gdx.audio.newSound(Gdx.files.internal("explohd.wav"));
    public static Sound gameOverSound = Gdx.audio.newSound(Gdx.files.internal("gameoversound.wav"));
    // Sound explosionPlayer = Gdx.audio.newSound(Gdx.files.internal("explosionCrunch_001.ogg"));


    //timing
    private float[] backgroundOffsets = {0, 0, 0, 0};
    private final float backgroundMaxScrollingSpeed;
    private float enemySpawnTimer = 0;
    private float enemyTankTimer = 0;

    //world parameters
    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;

    //game objects
    private final PlayerShip playerShip;
    RapidFireBoss rapidFireBoss;

    private final LinkedList<EnemyShip> enemyShipList;
    private final LinkedList<EnemyTank> enemyTankShipList;
    private final LinkedList<Laser> playerLaserList;
    private final LinkedList<Laser> enemyLaserList;
    private final LinkedList<Laser> tankLaserList;
    private final LinkedList<Laser> rapidFireLaserList;
    private LinkedList<Rocket> rocketLinkedList;
    private final LinkedList<Explosion> explosionList;
    private LinkedList<SparkEffect> sparkList;
    private LinkedList<WarpEffect> warpList;

    // SHIPS DETAILS
    private final float xCenter = WORLD_WIDTH / 2;
    private final float yCenter = WORLD_HEIGHT / 4;
    private float fireSpeed = 0.3f;


    // invulnerability related


    // SCORE METHOD


    // DISPLAY THINGGIES
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudCentreY, hudRow1Y, hudRow2Y, hudSectionWidth;

    public GameScreen(Game game) {

        this.game = game;

        // SCREEN
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //set up the texture atlas
        TextureAtlas textureAtlas = new TextureAtlas("images.atlas");
        TextureAtlas textureAtlas1 = new TextureAtlas("img.atlas");
        TextureAtlas textureAtlas2 = new TextureAtlas("laser-blue.atlas");
        TextureAtlas textureAtlas3 = new TextureAtlas("laser-pink.atlas");

        //setting up the background
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas1.findRegion("starb");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundHeight = WORLD_HEIGHT * 2;
        backgroundMaxScrollingSpeed = (float) (WORLD_HEIGHT) / 4;

        //initialize texture regions

        TextureRegion playerShipTextureRegion = textureAtlas1.findRegion("player_ship");
        enemyShipTextureRegion = textureAtlas1.findRegion("enemy");
        enemyTankTextureRegion = textureAtlas1.findRegion("enemytank");

        TextureRegion rapidFireBossTextureRegion = textureAtlas1.findRegion("enemyboss");
        TextureRegion playerShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion.flip(false, true);

        TextureRegion playerLaserTextureRegion = textureAtlas2.findRegion("drop");
        TextureRegion rocketTextureRegion = textureAtlas1.findRegion("rocket");
        enemyLaserTextureRegion = textureAtlas1.findRegion("enemy_laser");
        rapidFireBossLaserTextureRegion = textureAtlas3.findRegion("broad-one");
        rapidFireBossLaserTextureRegion.flip(false, true);

        explosionTexture = new Texture("hdexplo.png");
        sparkTexture = new Texture("spark.png");
        warpTexture = new Texture("Warp-effect.png");

        // initialize healthbar of boss

        // healthBarTexture = new Texture("HBOSS.png");

        //set up game objects

        // player

        playerShip = new PlayerShip(xCenter, yCenter,
                6, 6,
                40, 5,
                1f, 8, 100, fireSpeed / 3,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion, rocketTextureRegion);

        // boss

        rapidFireBoss = new RapidFireBoss(WORLD_WIDTH / 2, WORLD_HEIGHT + 1,
                45, 30,
                2, 1000, 6, 5, 50, 2f,
                rapidFireBossTextureRegion, enemyShieldTextureRegion, rapidFireBossLaserTextureRegion);


        // list
        enemyShipList = new LinkedList<>();
        enemyTankShipList = new LinkedList<>();


        playerLaserList = new LinkedList<>();
        rocketLinkedList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();
        tankLaserList = new LinkedList<>();
        rapidFireLaserList = new LinkedList<>();

        explosionList = new LinkedList<>();
        sparkList = new LinkedList<>();
        warpList = new LinkedList<>();

        batch = new SpriteBatch();

        prepareHUD();
    }

    public void prepareHUD() {
        //CREATE FROM FONT FILE
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EOF.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1, 1, 1, 0.3f);
        fontParameter.borderColor = new Color(0, 0, 0, 0.3f);

        font = fontGenerator.generateFont(fontParameter);


        //SCALING

        font.getData().setScale(0.08f);


        //CALCULATE HUD MARGIN
        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3;
        hudCentreY = 8;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();
        //scrolling background
        renderBackground(deltaTime);

        // MUSIC

        ambiant.setVolume(0.2f);
        ambiant.setLooping(true);
        ambiant.play();

        // keys, mouse and touching inputs

        detectInput(deltaTime);

        // player
        playerShip.update(deltaTime);

        // boss
        rapidFireBoss.update(deltaTime);
        //updateAndRenderHealthBar(deltaTime);

        // effects

        updateAndRenderSpark(deltaTime);
        updateAndRenderWarp(deltaTime);


        // make ennemies

        spawnEnemyShips(deltaTime);
        spawnEnemyTank(deltaTime);


        for (EnemyShip enemyShip : enemyShipList) {
            moveEnemy(enemyShip, deltaTime);

            enemyShip.update(deltaTime);

            //enemy ships

            enemyShip.draw(batch);


        }
        for (EnemyTank enemyTank : enemyTankShipList) {
            moveEnemyTank(enemyTank, deltaTime);

            enemyTank.update(deltaTime);

            //enemy ships

            enemyTank.draw(batch);


        }

        //player ship
        playerShip.draw(batch);

        // boss ship
        moveRapidFireBoss(rapidFireBoss, deltaTime);
        rapidFireBoss.update(deltaTime);
        rapidFireBoss.draw(batch);

        //
//        enemyBossShipRapidFire.draw(batch);

        //lasers

        renderLasers(deltaTime);

        //detect collisions between lasers and ships
        detectCollisions();

        //explosions
        updateAndRenderExplosions(deltaTime);


        // HUD RENDERING
        updateAndRenderHUD();




        batch.end();
    }

    private void updateAndRenderHUD() {
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Shield", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);
        font.draw(batch, "welcome to space", hudCentreX, hudCentreY, hudSectionWidth, Align.center, false);


        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y,
                hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield),
                hudCentreX, hudRow2Y, hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.lives),
                hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);

    }


    // SPAWN ENNEMIES ////

    private void spawnEnemyShips(float deltaTime) {
        enemySpawnTimer += deltaTime;

        float timeBetweenEnemySpawns = 1.5f;
        if (enemySpawnTimer > timeBetweenEnemySpawns && enemyShipList.size() < 5) {

            enemyShipList.add(new EnemyShip(SpaceShooterGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                    WORLD_HEIGHT * 8 / 10,
                    6, 6,
                    10, 3,
                    2f, 2, 40, 1.5f,
                    enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));
            warpList.add(new WarpEffect(warpTexture, new Rectangle(enemyShipList.getLast().boundingBox), 0.6f));
            enemySpawnTimer -= timeBetweenEnemySpawns;

        }
    }

    private void spawnEnemyTank(float deltaTime) {
        enemyTankTimer += deltaTime;

        float timeBetweenTankSpawns = 20f;
        if (enemyTankTimer > timeBetweenTankSpawns && enemyTankShipList.size() < 3) {

            enemyTankShipList.add(new EnemyTank(SpaceShooterGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                    WORLD_HEIGHT * 8 / 10, 10, 10,
                    4, 15, 4f, 4, 20, 3f,
                    enemyTankTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));
            warpList.add(new WarpEffect(warpTexture, new Rectangle(enemyTankShipList.getLast().boundingBox), 0.6f));
            enemyTankTimer -= timeBetweenTankSpawns;

        }
    }


    private void detectInput(float deltaTime) {

        //KEYBOARD INPUT


        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = WORLD_HEIGHT - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {

            playerShip.translate(Math.min(playerShip.movementSpeed * deltaTime, rightLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {

            playerShip.translate(0f, Math.min(playerShip.movementSpeed * deltaTime, upLimit));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {

            playerShip.translate(Math.max(-playerShip.movementSpeed * deltaTime, leftLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {

            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * deltaTime, downLimit));
        }



        // SCREEN TOUCHED OR MOUSE CLIC

        if(Gdx.input.isTouched()){
            // get screen pos
            float xTouchPxl = Gdx.input.getX();
            float yTouchPxl = Gdx.input.getY();
            // convert world pos
            Vector2 touchPoint = new Vector2(xTouchPxl,yTouchPxl);
            touchPoint = viewport.unproject((touchPoint));

            // calculate x y
            Vector2 playerShipCenter = new Vector2(
                    playerShip.boundingBox.x+playerShip.boundingBox.width/2,
                      playerShip.boundingBox.y+ playerShip.boundingBox.height/2);

            float touchDistance = touchPoint.dst(playerShipCenter);

            float TOUCH_MOVEMENT = 0.5f;
            if(touchDistance > TOUCH_MOVEMENT){
                float xTouchdiff = touchPoint.x - playerShipCenter.x ;
                float yTouchdiff = touchPoint.y - playerShipCenter.y;


            //scale to the max speed
            float xMove = (float) (xTouchdiff / touchDistance * playerShip.movementSpeed*1.2*deltaTime);
            float yMove = (float) (yTouchdiff / touchDistance * playerShip.movementSpeed*1.2*deltaTime);

            if(xMove>0) xMove = Math.min(xMove,rightLimit);
            else xMove = Math.max(xMove,leftLimit);

            if(yMove>0) yMove = Math.min(yMove,upLimit);
            else yMove = Math.max(yMove,downLimit);

            playerShip.translate(xMove,yMove);

            }


        }



    }

    // RANDOM ENEMIES MOVEMENT

    public void moveEnemy(EnemyShip enemyShip, float deltaTime){
        float leftLimit,rightLimit,upLimit,downLimit;
        leftLimit= -enemyShip.boundingBox.x;
        downLimit= (float)WORLD_HEIGHT/2 -enemyShip.boundingBox.y;
        rightLimit= WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT*8/10 - enemyShip.boundingBox.y - enemyShip.boundingBox.height;
        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed*deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed*deltaTime;

        if(xMove>0) xMove = Math.min(xMove,rightLimit);
        else xMove = Math.max(xMove,leftLimit);

        if(yMove>0) yMove = Math.min(yMove,upLimit);
        else yMove = Math.max(yMove,downLimit);

        enemyShip.translate(xMove,yMove);
    }
    public void moveEnemyTank(EnemyTank enemyTank, float deltaTime){
        float leftLimit,rightLimit,upLimit,downLimit;
        leftLimit= -enemyTank.boundingBox.x;
        downLimit= (float)WORLD_HEIGHT/3 -enemyTank.boundingBox.y;
        rightLimit= WORLD_WIDTH - enemyTank.boundingBox.x - enemyTank.boundingBox.width;
        upLimit = WORLD_HEIGHT*8/10 - enemyTank.boundingBox.y - enemyTank.boundingBox.height;
        float xMove = enemyTank.getDirectionVector().x * enemyTank.movementSpeed*deltaTime;
        float yMove = enemyTank.getDirectionVector().y * enemyTank.movementSpeed*deltaTime;

        if(xMove>0) xMove = Math.min(xMove,rightLimit);
        else xMove = Math.max(xMove,leftLimit);

        if(yMove>0) yMove = Math.min(yMove,upLimit);
        else yMove = Math.max(yMove,downLimit);

        enemyTank.translate(xMove,yMove);
    }
    public void moveRapidFireBoss(RapidFireBoss rapidFireBoss, float deltaTime){
        float leftLimit,rightLimit;
        leftLimit= -rapidFireBoss.boundingBox.x;
        rightLimit= WORLD_WIDTH - rapidFireBoss.boundingBox.x - rapidFireBoss.boundingBox.width;
        float xMove = rapidFireBoss.getDirectionVector().x * rapidFireBoss.movementSpeed*deltaTime;
        float yMove = rapidFireBoss.getDirectionVector().y * rapidFireBoss.movementSpeed*deltaTime;

        if(xMove>0) xMove = Math.min(xMove,rightLimit);
        else xMove = Math.max(xMove,leftLimit);
        rapidFireBoss.bossTranslation(xMove,yMove);
    }

    private void detectCollisions() {
        //for each player laser, check whether it intersects an enemy ship
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            ListIterator<EnemyTank> enemyTankListIterator = enemyTankShipList.listIterator();
            while(enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();
                if (enemyShip.intersects(laser.boundingBox)) {
                    score+=5;
                    //contact with enemy ship
                    if(enemyShip.hitAndCheckIfDead(laser)){
                        score+=50;
                        explosionSound.play(0.1f,0.9f,0f);
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture,new Rectangle(enemyShip.boundingBox),1.2f));

                    }
                    laserListIterator.remove();
                    break;
                }
            }
            while(enemyTankListIterator.hasNext()) {
                EnemyTank enemyTank = enemyTankListIterator.next();
                if (enemyTank.intersects(laser.boundingBox)) {

                    score+=5;
                    //contact with enemy tank ship
                    if(enemyTank.hitAndCheckIfDead(laser)){
                        score+=500;
                        explosionSound.play(0.1f,0.9f,0f);
                        enemyTankListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture,new Rectangle(enemyTank.boundingBox),1.2f));

                    }
                    laserListIterator.remove();
                    break;
                }
            }

            if(rapidFireBoss.intersects(laser.boundingBox)) {
                score +=5;
                if(rapidFireBoss.hitAndCheckIfDead(laser)){
                    score +=15000;
                    explosionSound.play(0.2f,0.9f,0f);
                    explosionList.add(new Explosion(explosionTexture,new Rectangle(rapidFireBoss.boundingBox),1.2f));
                    explosionList.add(new Explosion(explosionTexture,new Rectangle(rapidFireBoss.boundingBox),1.2f));
                    explosionList.add(new Explosion(explosionTexture,new Rectangle(rapidFireBoss.boundingBox),1.2f));
                    rapidFireBoss.boundingBox.x = 10000;
                    rapidFireBoss.boundingBox.y = 10000;
                    game.setScreen(new ca.shaxomann.spaceshooter.Screens.GameOverScreen(game,score));





                }
                laserListIterator.remove();
                break;
            }
        }


        //for each enemy laser, check whether it intersects the player ship
        laserListIterator = enemyLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                //contact with player ship
                if(playerShip.hitAndCheckIfDead(laser)){
                    explosionList.add(new Explosion(explosionTexture,new Rectangle(playerShip.boundingBox),1.6f));
                    playerShip.lives--;
                    explosionSound.play(0.2f,1f,0f);
                    playerShip.shield = 5;
                    playerShip.boundingBox.setPosition(xCenter,yCenter);
                    if(playerShip.lives == 0){
                        playerShip.death();
                        ambiant.stop();
                        game.setScreen(new GameOverScreen(game, score));


                    }




                }

                laserListIterator.remove();
            }
        }
    }



 /*   private void updateAndRenderHealthBar(float deltaTime){
        batch.draw(healthBarTexture,(float)WORLD_WIDTH/9 -14,-8,84,30);
    } */


    private void updateAndRenderExplosions(float deltaTime) {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while(explosionListIterator.hasNext()){
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if(explosion.isFinished()){
                explosionListIterator.remove();
            }
            else{
                explosion.draw(batch);
            }
        }

    }
    private void updateAndRenderSpark(float deltaTime) {
        ListIterator<SparkEffect> sparkListIterator = sparkList.listIterator();
        while(sparkListIterator.hasNext()){
            SparkEffect sparkEffect = sparkListIterator.next();
            sparkEffect.update(deltaTime);
            if(sparkEffect.isFinished()){
               sparkListIterator.remove();
            }
            else{
                sparkEffect.draw(batch);
            }
        }

    }
    private void updateAndRenderWarp(float deltaTime) {
        ListIterator<WarpEffect> warpListIterator = warpList.listIterator();
        while(warpListIterator.hasNext()){
            WarpEffect warpEffect = warpListIterator.next();
            warpEffect.update(deltaTime);
            if(warpEffect.isFinished()){
                warpListIterator.remove();
            }
            else{
                warpEffect.draw(batch);
            }
        }

    }



    private void renderLasers(float deltaTime) {


        //create new lasers
        //player lasers
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            sparkList.add(new SparkEffect(sparkTexture,new Rectangle(playerShip.boundingBox),fireSpeed*1));
            playerLaserList.addAll(Arrays.asList(lasers));
            laserSoundPlayer.play(0.01f, 1.8f,0f);


        }



        //enemy lasers
        for (EnemyShip enemyShip : enemyShipList) {
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }
        }

        // tank lasers
        for (EnemyTank enemyTank : enemyTankShipList) {
            if (enemyTank.canFireLaser()) {
                Laser[] lasers = enemyTank.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }
        }
        // boss
            if (rapidFireBoss.canFireLaser()) {
                Laser[] lasers = rapidFireBoss.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }

            // rocket











        //draw lasers
        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
        iterator = tankLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
        iterator = rapidFireLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void renderBackground(float deltaTime) {

        //update position of background images
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 100;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed;

        //draw each background layer
        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer],
                    WORLD_WIDTH, backgroundHeight);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {


    }

    @Override
    public void dispose() {
        batch.dispose();
        ambiant.dispose();

    }

}
