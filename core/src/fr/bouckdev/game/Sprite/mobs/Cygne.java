package fr.bouckdev.game.Sprite.mobs;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import fr.bouckdev.game.LpcAdventure;
import fr.bouckdev.game.Scenes.Hud;
import fr.bouckdev.game.Screens.PlayScreen;
import fr.bouckdev.game.Sprite.Joueur;

public class Cygne extends Mobs {

	private float stateTime;
	private Animation<TextureRegion> walkAnimation;
	private Array<TextureRegion> frames;
	private boolean setToDestroy;
	private boolean destroyed;

	
	public Cygne(PlayScreen screen, float x, float y) {
		super(screen, x, y,true);
		frames = new Array<TextureRegion>();
		for(int i = 0; i < 2; i++) 	
			frames.add(new TextureRegion(screen.getAtlas().findRegion("Entites"), 384 + i * 16, 0 , 16 ,16));
		walkAnimation = new Animation(0.25f, frames);
		stateTime = 0;
		setBounds(getX(), getY(), 16 / Joueur.PPM, 16 / Joueur.PPM);
		setToDestroy = false;
		destroyed = false;
	}
	

	
	public void update(float dt)  {
		stateTime += dt;	
		if(setToDestroy && !destroyed) {	
			world.destroyBody(b2body);
			destroyed = true;
			setRegion(new TextureRegion(screen.getAtlas().findRegion("Entites"), 416, 0, 16 ,16));
			stateTime = 0;		
		}
		
		else if(!destroyed) {
			b2body.setLinearVelocity(velocityCygne);
			setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
	        setRegion(getFrame(dt));
		}
	}
	
	@Override
	protected void defineMob() {
		
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / Joueur.PPM);
		fdef.shape = shape;
		
		fdef.filter.categoryBits = LpcAdventure.MOBS_CYGNE_BIT;
		fdef.filter.maskBits = LpcAdventure.GROUND_BIT | LpcAdventure.MOBS_BIT | LpcAdventure.CYGNEBLOC_BIT | LpcAdventure.TROUSSE_BIT | LpcAdventure.BRICK_BIT | LpcAdventure.OBJECT_BIT | LpcAdventure.JOUEUR_BIT;
		
		b2body.createFixture(fdef).setUserData(this);	
		
		//creation de la t�te
		
		PolygonShape head = new PolygonShape();
		Vector2[] vertice = new Vector2[4];
		vertice[0] = new Vector2(-5, 8).scl(1 / Joueur.PPM);
		vertice[1] = new Vector2(5, 8).scl(1 / Joueur.PPM);
		vertice[2] = new Vector2(-3, 3).scl(1 / Joueur.PPM);
		vertice[3] = new Vector2(3, 3).scl(1 / Joueur.PPM);
		head.set(vertice);
		
		fdef.shape = head;
		fdef.restitution = 0.5f;
		fdef.filter.categoryBits = LpcAdventure.MOBS_CYGNEHEAD_BIT;
		b2body.createFixture(fdef).setUserData(this);
	}
	
	
	@Override
	public void hitOnHead() {}
	
	public void draw(Batch batch) {	
		if(!destroyed|| stateTime < 1) 
			super.draw(batch);
	}
	
    public TextureRegion getFrame(float dt){
        TextureRegion region;
        region = walkAnimation.getKeyFrame(stateTime, true);
        
        if(velocityCygne.x > 0 && region.isFlipX() == true){
            region.flip(true, false);
        }
        if(velocityCygne.x < 0 && region.isFlipX() == false){
            region.flip(true, false);
        }
        return region;
    }



	@Override
	public void hitOnHeadCygne(Joueur player) {
		player.bounce();
		Hud.addScore(300);
		setToDestroy = true;
		LpcAdventure.manager.get("audio/sounds/death.ogg",Sound.class).play();	
	}
}
